(function($){
    $.fn.imageUploader = function (options) {
        // 디폴트 세팅
        let defaults = {
            preloaded: [],
            preloadedImageIdsKey: 'ImageIds',
            preloadedImageSrcKey: 'src',
            preloadedDeletingAjax: function () {},
            uploadingImageFilesKey: 'imageFiles',
            placeholder: '업로드하려면 파일을 끌어다 놓거나 여기를 클릭하세요',
            extensions: ['.jpg', '.jpeg', '.png', '.gif', '.svg'],
            mimes: ['image/jpeg', 'image/png', 'image/gif', 'image/svg+xml'],
            maxSize: 20 * 1024 * 1024, // 20 MB,
            maxFiles: undefined
        };
        // 플러그인 인스턴스 변수 할당
        let plugin = this;
        let dataTransfer = new DataTransfer();
        let $tempInput;

        plugin.init = function () {
            plugin.settings = $.extend({}, defaults, options);
            plugin.each(function (i, wrapper) {
                $imageUploader().appendTo(wrapper);
            });
        }

        const $imageUploader = function () {
            // 업로더 생성
            let $div_imageUploader = $(
                '<div class="image-uploader">' +
                '   <div class="placeholder">' +
                '       <span>' + plugin.settings.placeholder + '</span>' +
                '   </div>' +
                '</div>'
            );
            // 이미지 미리보기 카드 컨테이너 (sortable)
            let $div_cardContainer = $('<div class="card-container">');
            $div_imageUploader.append($div_cardContainer);
            // 업로더 이벤트 추가
            $div_imageUploader.on('dragover', fileDragoverEvent.bind($div_imageUploader));
            $div_imageUploader.on('dragleave', fileDragoverEvent.bind($div_imageUploader));
            $div_imageUploader.on('drop', fileSelectEvent.bind($div_imageUploader));
            $div_imageUploader.on('click', function (e){
                prevent(e);
                $tempInput = $('<input type="file" multiple="" name="temp">');
                $tempInput.on('click', function (ex) {ex.stopPropagation();} );
                $tempInput.on('change', fileSelectEvent.bind($div_imageUploader));
                $tempInput.on('change', function (){$tempInput = null});
                $div_imageUploader.append($tempInput);
                $tempInput.trigger('click');
            })

            if (plugin.settings.preloaded.length) {
                for (let i = 0; i < plugin.settings.preloaded.length; i++) {
                    $div_imageUploader.addClass('has-files');
                    $div_cardContainer.append($preloadedImageCard(i, plugin.settings.preloaded[i]));
                }
            }
            $div_cardContainer.sortable({update: function(e, ui) { arrangeCardsIndex($div_cardContainer); }});
            return $div_imageUploader;
        }
        // 기존 상품 이미지 카드
        const $preloadedImageCard = function (index, preloaded) {
            let $imageCard = $(
                '<div class="image-card" data-index="'+index+'" data-preloaded="true">' +
                '   <input ' +
                '       type="hidden"' +
                '       value="'+preloaded.id+'" ' +
                '       name="'+plugin.settings.preloadedImageIdsKey+'" ' +
                '       data-index="'+index+'">' +
                '   <img src="/'+preloaded[plugin.settings.preloadedImageSrcKey]+'">' +
                '</div>');

            $deleteImageCardButton(preloaded.id).appendTo($imageCard);
            $imageCard.on("click", function (e) { prevent(e); });
            return $imageCard;
        };
        // 추가로 업로드되는 이미지 카드
        const $uploadingImageCard = function (index, file) {
            let $imageCard = $(
                '<div class="image-card" data-index="'+index+'" data-preloaded="true">' +
                '   <img src="' + URL.createObjectURL(file) + '">' +
                '</div>'
            );
            let $input = $tempInput;
            $input.attr('name', plugin.settings.uploadingImageFilesKey+'[]')
            $imageCard.append($input);

            $deleteImageCardButton(null).appendTo($imageCard);
            $imageCard.on("click", function (e) { prevent(e); });
            return $imageCard;
        };
        // 삭제 버튼
        const $deleteImageCardButton = function (id) {
            let $deleteBtn = $('<button class="delete-image"><i class="iui-close"></i></button>')
            $deleteBtn.on("click", function (e) {
                prevent(e);
                let $imageCard = $(this).parent('.image-card');
                let $cardContainer = $imageCard.parent('.card-container');
                if ($imageCard.data('preloaded') === true) {
                    plugin.settings.preloaded = plugin.settings.preloaded.filter(function (p) {
                        return p.id !== id;
                    });
                    console.log(id);
                    plugin.settings.preloadedDeletingAjax(id);
                } else {
                    $imageCard.remove();
                }
                if (!$cardContainer.children().length) {
                    $cardContainer.parent().removeClass('has-files');
                }
            });
            return $deleteBtn;
        };
        const fileDragoverEvent = function (e) {
            prevent(e);
            if (e.type === "dragover") {
                $(this).addClass('drag-over');
            } else $(this).removeClass('drag-over');
        };
        const fileSelectEvent = function (e) {
            prevent(e);
            let $imageUploader = $(this);
            let files;
            if (e.currentTarget.files) {
                files = Array.from(e.currentTarget.files);
            } else if (e.originalEvent.dataTransfer.files) {
                files = Array.from(e.originalEvent.dataTransfer.files);
            }

            let validFiles = [];
            $(files).each(function (i, file) {
                if (plugin.settings.extensions && !validateExtension(file)) return;
                if (plugin.settings.mimes && !validateMIME(file)) return;
                if (plugin.settings.maxSize && !validateMaxSize(file)) return;
                if (plugin.settings.maxFiles && !validateMaxFiles(validFiles.length, file)) return;
                validFiles.push(file);
            });
            if (validFiles.length) {
                $imageUploader.removeClass('drag-over');
                createCardsFromFiles($imageUploader, validFiles);
            }
        };
        const createCardsFromFiles = function ($imageUploader, files) {
            $imageUploader.addClass('has-files');
            let $cardContainer = $imageUploader.find('.card-container');
            const existingCardsCount = arrangeCardsIndex($cardContainer)
            // Run through the files
            $(files).each(function (i, file) {
                dataTransfer.items.add(file)
                $cardContainer.append($uploadingImageCard(existingCardsCount + i, file));
                dataTransfer.clearData();
            });
        };
        const validateExtension = function (file) {
            if (plugin.settings.extensions.indexOf(file.name.replace(new RegExp('^.*\\.'), '.')) < 0) {
                alert(`다음 확장자만 업로드가 가능합니다. "${plugin.settings.extensions.join('", "')}"`);
                return false;
            }
            return true;
        };
        const validateMIME = function (file) {
            if (plugin.settings.mimes.indexOf(file.type) < 0) {
                alert(`파일 형식(${plugin.settings.mimes.join('", "')})만 업로드 가능합니다.`);
                return false;
            }
            return true;
        };
        const validateMaxSize = function (file) {
            if (file.size > plugin.settings.maxSize) {
                alert(`최대 ${plugin.settings.maxSize / 1024 / 1024}MB 까지만 업로드 가능합니다.`);
                return false;
            }
            return true;
        };
        const validateMaxFiles = function (index, file) {
            if ((index + dataTransfer.items.length + plugin.settings.preloaded.length) >= plugin.settings.maxFiles) {
                alert(`최대 ${plugin.settings.maxFiles}개 까지 업로드 가능합니다.`);
                return false;
            }
            return true;
        };
        const arrangeCardsIndex = function ($div_cardContainer) {
            let cards = $div_cardContainer.find('.image-card');
            const count = cards.length;
            for (let i = 0; i < count; i++) {
                cards[i].setAttribute('data-index', i)
            }
            return count;
        };
        const prevent = function (e) {
            // Prevent browser default event and stop propagation
            e.preventDefault();
            e.stopPropagation();
        };
        this.init();
        return this;
    };

}(jQuery));