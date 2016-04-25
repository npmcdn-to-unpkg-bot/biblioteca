angular.module('architectplay')
    .controller('artigo.list.controller', function ($scope, $rootScope, Artigo, toastr, $routeParams, $location) {

    $rootScope.title = 'Artigos';

//    $scope.mostrar = false;
//
//    Usuario.getAutenticado(function(data) {
//        $rootScope.usuario = data;
//        $scope.mostrar = true;
//    },function(data) {
//        $scope.mostrar = false;
//        $location.path('/');
//        toastr.error('Não autorizado');
//    });

    $scope.init = function() {
        $scope.nomeFiltro = '';
        $scope.filtrados = 0;

        Artigo.getAll(function(data) {
           $scope.artigos = data;
           $scope.quantidade = $scope.artigos.length;
        }, function(data) {
            toastr.error('Não autorizado.');
        });
    };
});