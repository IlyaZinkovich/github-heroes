(function () {
  $.get("/badges?receiverLogin=IlyaZinkovich", function (data) {
    console.log(data);
    data.forEach(function(badge) {
      $("#badges").append("<p>" + JSON.stringify(badge) + "</p>")
    });
  });
}());