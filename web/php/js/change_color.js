function altRows(id){
    if(document.getElementsByTagName){

        var table = document.getElementById(id);
        var rows = table.getElementsByTagName("tr");

        for(i = 0; i < rows.length; i++){
            if(i % 2 == 0){
                rows[i].className = "evenrowcolor";
            }else {
                rows[i].className = "oddrowcolor";
            }
        }
    }
}

function mouseMoveAndOut(id) {
    if(document.getElementsByTagName){

        var table = document.getElementById(id);
        var rows = table.getElementsByTagName("tr");
        for(var i = 0; i < rows.length; i++) {
            rows[i].onmouseout = function() {
                altRows(id);
            };
            rows[i].onmouseover = function() {
                this.className = "overcolor";
            }
        }
    }
}

window.onload=function(){
        altRows('alternatecolorAll');
        mouseMoveAndOut('alternatecolorAll');
        altRows('alternatecolorAQI');
        mouseMoveAndOut('alternatecolorAQI');
    };
