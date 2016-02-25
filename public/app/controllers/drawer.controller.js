angular.module('architectplay')
  .controller('drawer', function ($scope, $route) {
    console.log('Controller drawer');
    $scope.$route = $route;
});