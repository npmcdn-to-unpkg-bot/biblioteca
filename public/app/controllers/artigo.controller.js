angular.module('architectplay')
    .controller('artigo.list.controller', function ($scope, $rootScope, Artigo, toastr, $routeParams, $location) {

    // $rootScope.title = 'Artigos';

    $scope.init = function() {
        $scope.nomeFiltro = '';
        $scope.filtrados = 0;

        Artigo.getAll(function(data) {
            $scope.artigos = data;
            $scope.quantidade = $scope.artigos.length;
        }, function(data) {
            $location.path('/');
            toastr.error('Não autorizado.');
        });
    };
});