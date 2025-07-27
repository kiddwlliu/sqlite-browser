<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\meta>
 */
class MetaFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        // $case = rand(1,6);
        // if ($case >= 1 && $case <= 3) {
            return [
                'label' => join(".", fake()->words()),
                'proj_code' => fake()->numberBetween(1, 100),
                'comment' => '',
            ];
        // }
    }
}
