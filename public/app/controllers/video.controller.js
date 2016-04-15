angular.module('architectplay')
    .controller('video.controller', function ($scope, $rootScope, Usuario, toastr, $location) {
        $rootScope.title = 'Videos';

        Usuario.getAutenticado(function(data) {
            $rootScope.usuario = data;
            mostrar = true;
        },function(data) {
            mostrar = false;
            $location.path('/');
            toastr.error('Não autorizado');
        });
});