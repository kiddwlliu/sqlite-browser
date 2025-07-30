<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Response;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;
use Illuminate\Support\Arr;
use Illuminate\Support\Collection;
use Illuminate\Support\Facades\File;
use Illuminate\Routing\Controller;

class DatabaseController extends Controller
{
    private function dbPath(): string
    {
        return base_path('../etc/db');
    }

    private function isValidDbName(string $db): bool
    {
        return preg_match('/^[a-zA-Z0-9_]+\.(sqlite|db)$/', $db);
    }

    private function getConnection(string $db)
    {
        $fullPath = $this->dbPath() . "/$db";

        if (!File::exists($fullPath)) {
            abort(404, "Database not found");
        }

        config(["database.connections.sqlitebrowser" => [
            'driver' => 'sqlite',
            'database' => $fullPath,
            'prefix' => '',
        ]]);

        return DB::connection('sqlitebrowser');
    }

    public function listDatabases()
    {
        $files = File::files($this->dbPath());

        $valid = collect($files)->map(fn($f) => $f->getFilename())
            ->filter(fn($name) => $this->isValidDbName($name))
            ->values();

        return response()->json($valid);
    }

    public function listTables(string $db)
    {
        if (!$this->isValidDbName($db)) {
            abort(400, "Invalid database name");
        }

        $conn = $this->getConnection($db);
        $tables = $conn->select("SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%' ORDER BY name");

        return response()->json(array_map(fn($t) => $t->name, $tables));
    }

    public function getTableContent(string $db, string $table)
    {
        if (!$this->isValidDbName($db) || !preg_match('/^[a-zA-Z0-9_]+$/', $table)) {
            abort(400, "Invalid name");
        }

        $conn = $this->getConnection($db);

        try {
            $data = $conn->table($table)->limit(500)->get();
            return response()->json($data);
        } catch (\Exception $e) {
            abort(400, "Failed to query table: " . $e->getMessage());
        }
    }

    public function getTableColumns(string $db, string $table)
    {
        if (!$this->isValidDbName($db) || !preg_match('/^[a-zA-Z0-9_]+$/', $table)) {
            abort(400, "Invalid name");
        }

        $conn = $this->getConnection($db);

        $columns = $conn->select("PRAGMA table_info('$table')");

        return response()->json($columns);
    }
}
