angular.module('architectplay')
  .controller('livro.controller', function ($rootScope, $log) {
    console.log('Controller Livro');
    $rootScope.title = 'Livros';
});