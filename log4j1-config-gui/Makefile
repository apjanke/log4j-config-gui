# This Makefile is just for building the release distribution.
# It's not needed for just building or running the program

.PHONY: dist clean

PROGRAM=log4j1-config-gui
VERSION=$(shell cat VERSION)
DISTDIR=dist/${PROGRAM}-${VERSION}
FILES=../README.md ../LICENSE ../doc target/*.jar

dist: target/${PROGRAM}-${VERSION}.jar target/${PROGRAM}-${VERSION}-javadoc.jar target/${PROGRAM}-${VERSION}-sources.jar
	rm -rf dist/*
	mkdir -p ${DISTDIR}
	cp -R $(FILES) $(DISTDIR)
	cd dist; tar czf ${PROGRAM}-${VERSION}.tgz --exclude='*.DS_Store' ${PROGRAM}-${VERSION}
	cd dist; zip -rq ${PROGRAM}-${VERSION}.zip ${PROGRAM}-${VERSION} -x '*.DS_Store'

clean:
	rm -rf dist/*
	rm -rf target/*