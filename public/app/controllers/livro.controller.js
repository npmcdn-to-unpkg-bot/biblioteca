angular.module('architectplay')
    .controller('livro.list.controller', function ($scope, $rootScope, Livro, toastr, $location) {

    // $rootScope.title = Messages('menu.top.title.5');

    $scope.init = function() {
        $scope.nomeFiltro = '';
        $scope.filtrados = 0;

        Livro.getAll(function(data) {
            $scope.livros = data;
            $scope.quantidade = $scope.livros.length;
        }, function(data) {
            $location.path('/');
            toastr.error('Não autorizado.');
        });
    };
});