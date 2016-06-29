angular.module('architectplay')
    .controller('topmenu', function ($scope, $route) {

        //para funcionar o selected do menu através do ngRoute
        $scope.$route = $route;

    });