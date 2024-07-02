

// const $header = $('#header');
// $header
//     .css('color', 'green')
//     .css('font-size', '56px')
//     .css('text-align', 'center');
// console.log($header[0]);

$(function() { // Dropdown toggle
    $('.dropdown-toggle').click(function() {
        $(this).next('.dropdown').slideToggle();
    });

    $(document).click(function(e)
    {
        var target = e.target;
        if (!$(target).is('.dropdown-toggle') && !$(target).parents().is('.dropdown-toggle'))
//{ $('.dropdown').hide(); }
        { $('.dropdown').slideUp(); }
    });
});

function updateModels() {
    var brandId = document.getElementById("brand").value;
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                var models = JSON.parse(xhr.responseText);
                var modelSelect = document.getElementById("model");
                modelSelect.options.length = 1; // remove all existing options except the default
                models.forEach(function(model) {
                    var option = document.createElement("option");
                    option.value = model.id;
                    option.text = model.modelName;
                    modelSelect.appendChild(option);
                });
                modelSelect.disabled = false; // enable the model drop-down
                // updateModelId(); // update the hidden input field with the selected model ID
            } else {
                console.log("Error fetching models");
            }
        }
    };
    xhr.open("GET", "/vehicles/models/" + brandId);
    xhr.send();
}

function updateModelId() {
    var selectedModelId = document.getElementById("model").value;
    document.getElementById("modelId").value = selectedModelId;
}

