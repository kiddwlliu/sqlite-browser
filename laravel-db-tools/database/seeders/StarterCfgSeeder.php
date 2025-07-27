<?php

namespace Database\Seeders;

use App\Models\StarterCfg;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class StarterCfgSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        StarterCfg::factory(5)->create();
    }
}
