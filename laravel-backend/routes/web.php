<?php

use Illuminate\Support\Facades\Route;

Route::get('/', function () {
    return view('welcome');
});

// Route::get('/', function() {
//     // return Redirect::to('../frontend/dist/index.html');
//     return Redirect::to('index.html');
// });

// Route::get('/', function () {
//     $path = public_path('index.html');
//     if (File::exists($path)) {
//         return Response::file($path);
//     }
//     abort(404);
// });

// Route::get('/{any}', function () {
//     return File::get(public_path('react/index.html'));
// })->where('any', '.*');

// Route::get('/test/{path}', function ($path) {
Route::get('/spa/{path?}', function ($path = null) {
    $base = realpath(base_path('../var/static/spa'));

    $filePath = $base . '/' . ($path ?: 'index.html');

    if (!file_exists($filePath)) {
        abort(404);
    }

    $mime = mime_content_type($filePath);
    return response()->file($filePath, ['Content-Type' => $mime]);
})->where('path', '.*');