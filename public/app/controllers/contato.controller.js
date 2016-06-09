angular.module('architectplay')
    .controller('contato.create.controller', function ($scope, $route, $rootScope, $log, Contato, toastr) {

    $rootScope.title = Messages('menu.top.title.8');

    $scope.save = function() {
        Contato.save($scope.contato, function(data) {
            toastr.success(Messages('client.send.email.message'));
            $route.reload();
        }, function(data) {
            toastr.error(Messages('app.error'));
        });
    };

});