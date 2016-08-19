angular.module('architectplay')
  .controller('home.controller', function ($scope, Contato, toastr, $state, Carousel) {

      $scope.Carousel = Carousel;
      
      $scope.save = function() {
          Contato.save($scope.contato, function(data) {
              toastr.success(Messages('client.send.email.message'));
              $state.reload();
          }, function() {
              toastr.error(Messages('app.error'));
          });
      };
});