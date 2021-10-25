var weatherApp = angular.module('weatherApp', []);
weatherApp.controller('WeatherCtrl', function ($scope, $http) {
    $scope.form = {};
    $scope.items = [];
    $scope.create = function () {
        var item = angular.copy($scope.form);
        $http.post('/thanhtoan', item).then(resp => {
            $scope.items=resp.data;
            alert("Thêm mới thành công")
        }).catch(error => {
            alert("Lỗi thêm mới sản phẩm");
            console.log("Error", error);
        });
    }
});