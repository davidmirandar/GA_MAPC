#!/bin/bash

cd $1
#~/Desktop/grafos

ls | grep -v -f $2 > lista #~/Desktop/arquivosSim > lista

xargs -t -L1 rm -f < lista