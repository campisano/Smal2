angular.module("ControllersModule").controller("ListLaboratoriesPresenter", [
    "$scope",
    "SessionService",
    "LaboratoryService",
    function($scope, SessionService, LaboratoryService)
    {
        // private
        var listLaboratories = function()
        {
            LaboratoryService.getLaboratories(
                SessionService.session_id,
                function(data)
                {
                    $scope.laboratories = data;
                },
                function(data) {
                    $scope.response = data;
                }
            );
        };

        // constructor
        {
            $scope.session = SessionService;
            $scope.command = listLaboratories;
            $scope.laboratories = [];
            $scope.response = "";

            $scope.command();
        };
    }
]);
