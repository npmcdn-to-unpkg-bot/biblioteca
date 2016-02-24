angular.module('architectplay')
  .controller('drawer', function ($scope, $location) {
    console.log('Controller drawer');
//    $scope.isActive = function (viewLocation) {
//         var active = (viewLocation === $location.url());
//         return active;
//    };
 // Must use a wrapper object, otherwise "activeItem" won't work
    $scope.states = {};
    $scope.states.activeItem = 'item1';
    $scope.items = [{
        id: 'item1',
        title: 'Home'
    }, {
        id: 'item2',
        title: 'Public Rooms'
    }, {
        id: 'item3',
        title: 'My Rooms'
    }];
});