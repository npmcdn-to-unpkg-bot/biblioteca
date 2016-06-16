angular.module('architectplay')
    .controller('foto.controller', function ($scope, $rootScope, Usuario, toastr, $location) {
        // $rootScope.title = Messages('menu.top.title.7');

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