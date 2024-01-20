var previousFile=null;

// File 타입은 immutable해서 값을 바꾸지 못하므로 새로 File 객체를 만들어서 연결하기 위한 메소드
function createFile(fileInput) {
    // File 타입은 immutable하기 때문에 새로 File을 만들어서 연결하려고 null로 초기화
    fileInput.value = null;
    // 새로운 파일을 생성하거나, 기존 파일을 복제하는 등의 작업을 수행
    var newFile = new File([previousFile], previousFile.name, { type: previousFile.type });

    // FileList를 생성하고 파일 인풋에 설정
    var newFileList = new DataTransfer();
    newFileList.items.add(newFile);
    fileInput.files = newFileList.files;

    // 파일 인풋에 change 이벤트 강제 트리거
    fileInput.dispatchEvent(new Event("changeImage"));
}

function changeImage() {
    var fileInput = document.getElementById("image");
    console.log('1 previousFile={}',previousFile);
    console.log('1 fileInput={}',fileInput.files[0]);

    // 이미지 파일만 선택되도록
    var allowedExtensions = ["jpg", "jpeg", "png"];
    if (fileInput.files.length > 0) {
        var fileName = fileInput.files[0].name;
        var fileExtension = fileName.split(".").pop().toLowerCase();

        if (allowedExtensions.indexOf(fileExtension) === -1) { // 이미지 형식이 아닐때
            alert(".jpg/.jpeg/.png 이미지 파일만 가능합니다");
            if (previousFile) { // previousFile!=null
                console.log('if(previousFile)');

                createFile(fileInput);
            } else { // previousFile==null
                console.log('if(!previousFile)');
                fileInput.value = null; // 파일 선택 초기화
            }
            console.log('previousFile=',previousFile);
            console.log('fileInput=',fileInput.files[0]);
            return;
        }
        previousFile=fileInput.files[0];
    }
    console.log('previousFile1=',previousFile)
    console.log('fileInput1=',fileInput.files[0])

    console.log('파일 선택');
    // 파일 선택에서 취소를 눌러 아무 파일을 선택하지 않았을때
    var selectedFile = fileInput.files[0];
    if (selectedFile === undefined) { // 파일을 선택하지 않았을 때
        createFile(fileInput);
        return;
    }
    previousFile=fileInput.files[0];
    console.log('previousFile2=',previousFile)
    console.log('fileInput2=',fileInput.files[0])

    // 파일 선택시 즉각적으로 img 태그에 렌더링되게
    if (fileInput.files && fileInput.files[0]) {
        console.log(fileInput.files)
        var reader = new FileReader();
        reader.onload = function (e) {
            // 이미지를 미리보기 이미지로 설정
            $("#profileImage").attr("src", e.target.result);
        };
        reader.readAsDataURL(fileInput.files[0]);
    }
}
// nickname.trim()을 한 후에 서버에 전달하려고
function trimNicknameInput() {
    var nicknameInput = document.getElementById('nickname');
    nicknameInput.value = nicknameInput.value.trim();
}