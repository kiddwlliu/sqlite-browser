<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

// https://fakerphp.org
//  - https://fakerphp.org/formatters/text-and-paragraphs/
//  - https://fakerphp.org/formatters/miscellaneous/
//    boolean
//  - https://fakerphp.org/formatters/#fakerprovideren_usperson
//    persion - name()

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\StarterCfg>
 */
class StarterCfgFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        // $table->id();
        // $table->string('name');         // variable name
        // $table->string('value');        //
        // $table->string('type');         // expected using type, string
        // $table->string('storeType');    // string(same), hexstring, base64string
        // $table->string('usage');        // copy
        // $table->string('tables');       // external refer tables
        // $table->string('users');        // external refer users
        // $table->string('comment');
        // $table->timestamps();

        $case = rand(1,6);
        if ($case >= 1 && $case <= 3) {
            // type == string
            return [
                'name' => join(".", fake()->words(rand(2,3))),
                'value' => fake()->word(),
                'type' => 'string',
                'storeType' => 'string',
                'usage' => fake()->randomElement(['dev', 'production', 'doc']),
                'tables' => join(",", fake()->randomElements(['k1', 'p2', 'd3', 'z4'], null)),
                'users' => '',
                'comment' => '',
            ];
        } else if ($case == 4) {
            // type == boolean
            return [
                'name' => join(".", fake()->words(rand(2,3))),
                'value' => fake()->randomElement(['true', 'false']),
                'type' => 'boolean',
                'storeType' => 'string',
                'usage' => fake()->randomElement(['dev', 'production', 'doc']),
                'tables' => join(",", fake()->randomElements(['k1', 'p2', 'd3', 'z4'], null)),
                'users' => '',
                'comment' => '',
            ];
        } else if ($case >= 5 && $case <= 6) {
            // type == integer
            return [
                'name' => join(".", fake()->words(rand(2,3))),
                'value' => strval(fake()->randomNumber(5, false)),
                'type' => 'integer',
                'storeType' => 'string',
                'usage' => fake()->randomElement(['dev', 'production', 'doc']),
                'tables' => join(",", fake()->randomElements(['k1', 'p2', 'd3', 'z4'], null)),
                'users' => '',
                'comment' => '',
            ];
        }
    }
}
