angular.module('architectplay')
    .controller('livro.controller', function ($scope, $rootScope, Usuario, toastr, $location) {
        $rootScope.title = 'Livros';

        Usuario.getAutenticado(function(data) {
            $rootScope.usuario = data;
            mostrar = true;
        },function(data) {
            mostrar = false;
            $location.path('/');
            toastr.error('Não autorizado');
        });
});