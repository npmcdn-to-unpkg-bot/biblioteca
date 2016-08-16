angular.module('architectplay')
    .controller('noticia.list.controller', function ($scope, Noticia, toastr) {
        
        $scope.init = function() {
            Noticia.getAll(function(data) {
                $scope.noticias = data;
            }, function() {
                toastr.error('Não autorizado.');
            });
        };
        
    });