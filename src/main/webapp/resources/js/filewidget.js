$(document).ready(function() {

//update on page load 
updateFileTable(); 

var fileupload = $('#fileupload').fileupload({
//		maxChunkSize: 10000000 //10MB - TODO not chunked has file size limit of 4GB
	replaceFileInput: false,
	maxNumberOfFiles: 1,
	add: function (e, data) {
        $('#selectedFile').val(data.files[0].name + " (" + bytesToSize(data.files[0].size) + ")");
		data.context = $('#startUpload') 
            .click(function () {
                fileupload = data.submit();
            });
    },
    done: function(e, data) {
    	updateFileTable();
    },
    progressall: function(e, data) { 
		var progress = parseInt(data.loaded / data.total * 100, 10);
		$('#progress').css(
		'width',
		progress + '%'
		);
		$('#progress').html(progress + "%");
    }
});
//}).on('fileuploadprogress', function(e, data) {
//	$("#progressBar").progressbar( "option", "value", data.loaded/data.total * 100);
//	$("#bitrateLbl").text(bytesToSize(data.loaded) + "/" + 
//		bytesToSize(data.total) + " - " + 
//		bytesToSize(data.bitrate/8) + '/s');
// handlers for chunked uploading 
//		.on('fileuploadchunksend', function (e, data) {})
//		.on('fileuploadchunkdone', function (e, data) {})
//		.on('fileuploadchunkfail', function (e, data) {})
//		.on('fileuploadchunkalways', function (e, data) {});

$('#cancelUpload').click(function(e) {
	fileupload.abort();
});

//Change colour on click
$('#fileTable').on('click', 'tbody tr', function(event) {
    $(this).addClass('highlight').siblings().removeClass('highlight');

    //first column contains filename
    var row = $(event.target.parentNode);
    streamer.selectedFile = row.children()[0].innerHTML;	
});

//$('#fileTable').click(function(e) {
//	$(this).find("tr").each(function() { //reset all rows to white
//		$(this).css('background-color', 'white')
//	});
//
//	//select clicked row
//	var row = $(e.target.parentNode);
//	row.css('background-color', '#F5F5F5');
//	
//});

function updateFileTable() {
	$.get('/Twintiment/files')
		.success(function(msg) {
			
			$('.fileTable tbody').children().remove(); //clear table
			
			msg.forEach(function(file) {
				//Add row to table
				$('#fileTable').find('tbody')
					.append($('<tr>')
							.append($('<td>')
								.append(file.fileName)
							.append($('</td>')))
							.append($('<td>')
									.append(bytesToSize(file.fileSize))
							.append($('</td>')))
					.append($('</tr>')));	
			}); //foreach
		}) //success
		.error(function(msg) {
			console.log(msg);
		});
}

function bytesToSize(bytes) {
	   var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
	   if (bytes == 0) return '0 Byte';
	   var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
	   return Number(bytes / Math.pow(1024, i), 2).toFixed(2) + ' ' + sizes[i];
	};

});