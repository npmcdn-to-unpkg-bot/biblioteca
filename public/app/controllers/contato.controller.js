angular.module('architectplay')
    .controller('contato.list.controller', function ($scope, $rootScope, Contato, toastr, $routeParams) {

    $rootScope.title = 'Fale conosco';

    $scope.init = function() {
        $scope.nomeFiltro = '';
        $scope.filtrados = 0;

        Contato.getAll(function(data) {
           $scope.contatos = data;
           $scope.quantidade = $scope.contatos.length;
        }, function(data) {
            toastr.error('Não autorizado.');
        });
    };

    $scope.busca = function() {

       if ($scope.nomeFiltro) {
            Contato.getFiltroContatos({filtro:$scope.nomeFiltro}, $scope.contato, function(data) {
                $scope.contatos = data;
                $scope.filtrados = $scope.contatos.length;
            }, function(data) {
                 toastr.error(data.data,'Não autorizado');
             });
       } else {
            Contato.getAll(function(data) {
                $scope.contatos = data;
            });
       };
    };
}).controller('contato.detail.controller', function ($scope, $rootScope, $routeParams, $location, Contato, toastr) {

    $rootScope.title = 'Fale conosco';

    $scope.init = function() {
        $scope.contato = Contato.get({id:$routeParams.id}, function(data) {
        },function(data) {
            toastr.error(data.data);
        });
    };

}).controller('contato.create.controller', function ($scope, $route, $rootScope, $log, Contato, toastr) {

    $rootScope.title = 'Fale conosco';

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