angular.module('architectplay')
    .controller('usuario.list.controller', function ($scope, $rootScope, Usuario, toastr, $routeParams) {

    $rootScope.title = 'Usuários';

    $scope.init = function() {
        $scope.nomeFiltro = '';
        $scope.filtrados = 0;

        Usuario.getAll(function(data) {
           $scope.usuarios = data;
           $scope.quantidade = $scope.usuarios.length;
        }, function(data) {
            toastr.error(data.data, 'Não autorizado.');
        });
    };

    $scope.busca = function() {

       if ($scope.nomeFiltro) {
            Usuario.getFiltroUsuarios({filtro:$scope.nomeFiltro}, $scope.usuario, function(data) {
                $scope.usuarios = data;
                $scope.filtrados = $scope.usuarios.length;
            }, function(data) {
                 toastr.error(data.data,'Não autorizado');
             });
       } else {
            Usuario.getAll(function(data) {
                $scope.usuarios = data;
            });
       };
    };

  }).controller('usuario.detail.controller', function ($scope, $rootScope, $routeParams, $location, Usuario, toastr, ngDialog) {

    $rootScope.title = 'Usuários';

    $scope.opendialog = function() {
        ngDialog.open({
            template: 'templateId',
            controller:'usuario.detail.controller'
        });
    };

    $scope.init = function() {
        $scope.usuario = Usuario.get({id:$routeParams.id}, function(data) {
        },function(data) {
            toastr.error(data.data);
        });
    };

    $scope.delete = function() {

      $scope.usuario = Usuario.get({id:$routeParams.id}, function(data) {
          $scope.usuarioExcluido = $scope.usuario.email;
      });

        Usuario.delete({id:$routeParams.id}, function() {
            toastr.warning('foi removido com Sucesso.', 'O usuário: ' + $scope.usuarioExcluido);
            $scope.closeThisDialog('Fechar')
            $location.path('/usuarios');
        }, function(data) {
            $scope.closeThisDialog('Fechar')
            toastr.error(data.data, 'Não foi possível Remover.');
        });
    };

    $scope.open = function (size) {

        $modalInstance = $modal.open({
              templateUrl: 'modalConfirmacao.html',
              controller: 'UsuarioDetailController',
              size: size,
        });
    };

    $scope.cancelModal = function () {
        $modalInstance.dismiss('cancelModal');
    };

  }).controller('usuario.edit.controller', function ($scope, $rootScope, $routeParams, $location, Usuario, toastr) {

    $rootScope.title = 'Usuários';

    $scope.init = function() {
        $scope.usuario = Usuario.get({id:$routeParams.id}, function(data) {
        },function(data) {
            toastr.error(data.data);
        });
    };

    $scope.update = function() {
        Usuario.update({id:$routeParams.id}, $scope.usuario, function(data) {
            toastr.info('foi atualizado com Sucesso.', 'O usuário: ' + $scope.usuario.email);
            $location.path('/usuarios');
        },function(data) {
           toastr.error(data.data, 'Não foi possível Atualizar.');
        });
    };

  }).controller('usuario.perfil.controller', function ($scope, $rootScope, $routeParams, $location, Usuario, toastr) {

    $scope.init = function() {
      Usuario.getAutenticado(function(data){
          $rootScope.usuario = data;
      });
    };

  });