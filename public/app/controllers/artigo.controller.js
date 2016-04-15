angular.module('architectplay')
    .controller('artigo.controller', function ($scope, $rootScope, Usuario, toastr, $location) {
        $rootScope.title = 'Artigos';

          Usuario.getAutenticado(function(data) {
              $rootScope.usuario = data;
              mostrar = true;
           },function(data) {
                mostrar = false;
                $location.path('/');
                toastr.error('Não autorizado');
             });
    });