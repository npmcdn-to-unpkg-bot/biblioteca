angular.module('architectplay')
    .controller('evento.list.controller', function ($scope, Evento, toastr, $location) {

        // $rootScope.title = Messages('menu.top.title.4');
        
        $scope.init = function() {
            Evento.getAll(function(data) {
                $scope.eventos = data;
            }, function() {
                $location.path('/');
                toastr.error('Não autorizado.');
            });
        };
    });