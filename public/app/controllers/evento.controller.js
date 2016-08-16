angular.module('architectplay')
    .controller('evento.list.controller', function ($scope, Evento, toastr) {
        $scope.init = function() {
            Evento.getAll(function(data) {
                $scope.eventos = data;
                $scope.diaSemana = function( data ){
                    return moment(data).format('LLLL').split(',')[0];
                };

                $scope.mes= function( data ){
                    return moment(data).format('lll').split(' de ')[1];
                };

                $scope.dia= function( data ){
                    return moment(data).format('L').split('/')[0];
                };
            }, function() {
                toastr.error(Messages('app.error'));
            });
        };
    });