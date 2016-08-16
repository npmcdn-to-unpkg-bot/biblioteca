angular.module('architectplay')
    .controller('publicacao.list.controller', function ($scope, Publicacao, toastr) {
        $scope.init = function() {
            Publicacao.getAll(function(data) {
                $scope.publicacoes = data;
            }, function() {
                toastr.error(Messages('app.error'));
            });
        };
    });