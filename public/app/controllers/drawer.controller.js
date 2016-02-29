angular.module('architectplay')
  .controller('drawer', function ($scope, $route) {
  //para funcionar o selected do menu através do ngRoute
    $scope.$route = $route;
});