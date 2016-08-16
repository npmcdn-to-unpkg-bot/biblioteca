angular.module('architectplay')
    .controller('foto.controller', function ($scope, $rootScope, Usuario, toastr) {
        $scope.mostrar = false;

        Usuario.getAutenticado(function(data) {
            $rootScope.usuario = data;
            $scope.mostrar = true;
        },function() {
            $scope.mostrar = false;
            toastr.error(Messages('app.error'));
        });
});