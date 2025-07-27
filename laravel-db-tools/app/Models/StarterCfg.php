<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class StarterCfg extends Model
{
    /** @use HasFactory<\Database\Factories\StarterCfgFactory> */
    use HasFactory;

    // php artisan db:seed, default table name => starter_cfgs
    /**
     * The table associated with the model.
     *
     * @var string
     */
    protected $table = 'starter_cfgs';

    // https://laravel.com/docs/11.x/eloquent-mutators#date-casting
    protected function casts(): array
    {
        return [
            'created_at' => 'datetime:Y-m-d H:i:s',
            'updated_at' => 'datetime:Y-m-d H:i:s',
        ];
    }
}
