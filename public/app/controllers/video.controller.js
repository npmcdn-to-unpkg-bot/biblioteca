angular.module('architectplay')
    .controller('video.list.controller', function ($scope, $rootScope, Video, toastr, $routeParams, $location) {
        // $rootScope.title = Messages('menu.top.title.6');
        
        $scope.init = function() {
            Video.getAll(function(data) {
                $scope.videos = data;
            }, function(data) {
                $location.path('/');
                toastr.error('Não autorizado.');
            });
        };
});