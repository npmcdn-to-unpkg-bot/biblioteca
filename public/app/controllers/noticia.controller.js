angular.module('architectplay')
    .controller('noticia.list.controller', function ($scope, $rootScope, Noticia, toastr, $routeParams, $location) {
        // $rootScope.title = Messages('menu.top.title.4');

        $scope.init = function() {
            Noticia.getAll(function(data) {
                $scope.noticias = data;
            }, function(data) {
                $location.path('/');
                toastr.error('Não autorizado.');
            });
        };
        
    });