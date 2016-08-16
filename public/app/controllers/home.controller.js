angular.module('architectplay')
  .controller('home.controller', function ($scope, Contato, toastr, $route) {
      $scope.save = function() {
          Contato.save($scope.contato, function() {
              toastr.success(Messages('client.send.email.message'));
              $route.reload();
          }, function() {
              toastr.error(Messages('app.error'));
          });
      };

});