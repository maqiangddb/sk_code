#ifndef STDAFX_H
#define STDAFX_H

#include <stdio.h>
#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <set>
#include <list>
#include <deque>
#include <utility>
#include <algorithm>
#include <functional>
#include <string.h>

using namespace std;

#ifdef __cplusplus
#define DLL_EXPORT extern "C"
#define DLL_IMPORT extern "C"
#else
#define DLL_EXPORT
#define DLL_IMPORT
#endif

#define uchar unsigned char
#define ushort unsigned short
#define uint unsigned int
#define RECOLLATE_ADDR_TIMES         10000



#endif // STDAFX_H
