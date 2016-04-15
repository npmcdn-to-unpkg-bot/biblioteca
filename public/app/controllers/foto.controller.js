angular.module('architectplay')
    .controller('foto.controller', function ($scope, $rootScope, Usuario, toastr, $location) {
        $rootScope.title = 'Fotos';

        Usuario.getAutenticado(function(data) {
            $rootScope.usuario = data;
            mostrar = true;
        },function(data) {
            mostrar = false;
            $location.path('/');
            toastr.error('Não autorizado');
        });
});