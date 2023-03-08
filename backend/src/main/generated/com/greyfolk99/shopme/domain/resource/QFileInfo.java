package com.greyfolk99.shopme.domain.resource;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileInfo is a Querydsl query type for FileInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QFileInfo extends BeanPath<FileInfo> {

    private static final long serialVersionUID = -1567403034L;

    public static final QFileInfo fileInfo = new QFileInfo("fileInfo");

    public final StringPath filename = createString("filename");

    public final StringPath originalFilename = createString("originalFilename");

    public final StringPath subPath = createString("subPath");

    public QFileInfo(String variable) {
        super(FileInfo.class, forVariable(variable));
    }

    public QFileInfo(Path<? extends FileInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileInfo(PathMetadata metadata) {
        super(FileInfo.class, metadata);
    }

}

