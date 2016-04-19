angular.module('architectplay')
    .controller('livro.controller', function ($scope, $rootScope, Usuario, toastr, $location) {
        $rootScope.title = 'Livros';

        $scope.mostrar = false;

        Usuario.getAutenticado(function(data) {
            $rootScope.usuario = data;
            $scope.mostrar = true;
        },function(data) {
            $scope.mostrar = false;
            $location.path('/');
            toastr.error('Não autorizado');
        });
});