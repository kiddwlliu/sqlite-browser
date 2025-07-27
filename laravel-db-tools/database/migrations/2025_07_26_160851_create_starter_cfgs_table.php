<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('starter_cfgs', function (Blueprint $table) {
            $table->id();
            $table->string('name');         // variable name
            $table->string('value');        //
            $table->string('type');         // expected using type, string
            $table->string('storeType');    // string(same), hexstring, base64string
            $table->string('usage');        // copy
            $table->string('tables');       // external refer tables
            $table->string('users');        // external refer users
            $table->string('comment');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('starter_cfgs');
    }
};
