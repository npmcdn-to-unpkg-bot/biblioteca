angular.module('architectplay')
  .controller('home.controller', function ($scope, $log, $timeout, cfpLoadingBar) {
      console.log('Controller Home');

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
          }, 100);*/
});