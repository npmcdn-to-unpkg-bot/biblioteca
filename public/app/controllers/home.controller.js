angular.module('architectplay')
  .controller('home.controller', function ($scope, $rootScope, $routeParams, cfpLoadingBar, Contato, $location, toastr, $route) {

      // $rootScope.title = Messages('menu.top.title.1');

      // fake the initial load so first time users can see the bar right away:
      /*$scope.start = function() {
        cfpLoadingBar.start();
      };

      $scope.complete = function () {
        cfpLoadingBar.complete();
      };

      //fake the initial load so first time users can see the bar right away:
      $scope.start();
      $scope.fakeIntro = true;
      $timeout(function() {
        $scope.complete();
        $scope.fakeIntro = false;
      }, 9000);*/

      $scope.save = function() {
          Contato.save($scope.contato, function(data) {
              toastr.success(Messages('client.send.email.message'));
              $route.reload();
          }, function(data) {
              toastr.error(Messages('app.error'));
          });
      };

});