angular.module('architectplay')
    .controller('artigo.list.controller', function ($scope, Artigo, toastr) {
        $scope.init = function() {
            Artigo.getAll(function(data) {
                $scope.artigos = data;
            }, function() {
                toastr.error(Messages('app.error'));
            });
        };
});