<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\DatabaseController;
// use App\Http\Controllers\VersionController;


Route::prefix('databases')->group(function () {
    Route::get('/', [DatabaseController::class, 'listDatabases']);
    Route::get('{db}/tables', [DatabaseController::class, 'listTables']);
    Route::get('{db}/tables/{table}', [DatabaseController::class, 'getTableContent']);
    Route::get('{db}/tables/{table}/columns', [DatabaseController::class, 'getTableColumns']);
});

// Route::get('/version', [VersionController::class, 'index']);