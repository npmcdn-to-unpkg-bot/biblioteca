angular.module('architectplay')
    .controller('artigo.list.controller', function ($scope, $rootScope, Artigo, toastr, $routeParams, $location) {

    // $rootScope.title = Messages('menu.top.title.4');

    $scope.init = function() {
        Artigo.getAll(function(data) {
            $scope.artigos = data;
        }, function(data) {
            $location.path('/');
            toastr.error('Não autorizado.');
        });
    };
});