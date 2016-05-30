angular.module('architectplay')
    .controller('contato.create.controller', function ($scope, $route, $rootScope, $log, Contato, toastr) {

    // $rootScope.title = 'Fale conosco';

    $scope.save = function() {
        Contato.save($scope.contato, function(data) {
            toastr.success('Mensagem enviada com Sucesso');
            $route.reload();
        }, function(data) {
            console.log(data);
            toastr.error(data.data,'Não foi possível Enviar');
        });
    };

});