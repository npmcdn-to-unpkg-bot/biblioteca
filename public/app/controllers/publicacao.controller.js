angular.module('architectplay')
    .controller('publicacao.list.controller', function ($scope, $rootScope, Publicacao, toastr, $routeParams, $location) {
        // $rootScope.title = Messages('menu.top.title.2');

        $scope.init = function() {
            Publicacao.getAll(function(data) {
                $scope.publicacoes = data;
            }, function(data) {
                $location.path('/');
                toastr.error('Não autorizado.');
            });
        };
    });