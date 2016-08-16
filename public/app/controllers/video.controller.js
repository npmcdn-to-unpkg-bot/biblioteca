angular.module('architectplay')
    .controller('video.list.controller', function ($scope, Video, toastr) {
        $scope.init = function() {
            Video.getAll(function(data) {
                $scope.videos = data;
            }, function() {
                toastr.error('Não autorizado.');
            });
        };
});