###############################################################################
#
# Generic Makefile for C/C++ Program
#
# Description:
# The makefile searches in <SRCDIRS> directories for the source files
# with extensions specified in <SOURCE_EXT>, then compiles the sources
# and finally produces the <ROGRAM>, the executable file, by linking
# the objectives.

# Usage:
#   $ make           compile and link the program.
#   $ make objs      compile only (no linking. Rarely used).
#   $ make clean     clean the objectives and dependencies.
#   $ make cleanall  clean the objectives, dependencies and executable.
#   $ make rebuild   rebuild the program. The same as make clean && make all.
#==============================================================================

## Customizing Section: adjust the following if necessary.
##=============================================================================

# The executable file name.
PROGRAM_1   = verifykey


# The directories in which source files reside.
# At least one path should be specified.
TOPDIR		= .
OPENSSLDIR	= ./openssl
SRCDIRS   =   ${TOPDIR}

# The source file types (headers excluded).
# At least one type should be specified.
# The valid suffixes are among of .c, .C, .cc, .cpp, .CPP, .c++, .cp, or .cxx.
SRCEXTS   = .c .cpp

# The flags used by the cpp (man cpp for more).
# CPPFLAGS  = -Wall -Werror # show all warnings and take them as errors
CPPFLAGS  += -I${TOPDIR}/openssl/include

# The compiling flags used only for C.
# If it is a C++ program, no need to set these flags.
# If it is a C and C++ merging program, set these flags for the C parts.
CFLAGS    = -o2

# The compiling flags used only for C++.
# If it is a C program, no need to set these flags.
# If it is a C and C++ merging program, set these flags for the C++ parts.
CXXFLAGS  = -o2 -g0

# The library and the link options ( C and C++ common).
LDFLAGS   = -ldl -L${OPENSSLDIR} -lssl -lcrypto

## Implict Section: change the following only when necessary.
##=============================================================================
# The C program compiler. Uncomment it to specify yours explicitly.
CC      = gcc

# The C++ program compiler. Uncomment it to specify yours explicitly.
CXX     = g++

# The command used to delete file.
RM        = rm -rf

## Stable Section: usually no need to be changed. But you can add more.
##=============================================================================
SHELL   = /bin/sh
SOURCES = $(foreach d,$(SRCDIRS),$(wildcard $(addprefix $(d)/*,$(SRCEXTS))))
OBJS    = $(foreach x,$(SRCEXTS), \
      $(patsubst %$(x),%.o,$(filter %$(x),$(SOURCES))))

#OBJS += $(PROGRAM_1).o

.PHONY : all objs clean cleanall rebuild

all : $(PROGRAM_1)

# Rules for producing the objects.
#---------------------------------------------------
objs : $(OBJS)

%.o : %.c
	$(CC) -c  $(CPPFLAGS) $(CFLAGS) $< -o $(patsubst %.c,%.o,$<)

%.o : %.C
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< -o $(patsubst %.C,%.o,$<)

%.o : %.cc
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< -o $(patsubst %.cc,%.o,$<)

%.o : %.cpp
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< -o $(patsubst %.cpp,%.o,$<)

%.o : %.CPP
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< -o $(patsubst %.CPP,%.o,$<)

%.o : %.c++
	$(CXX -c $(CPPFLAGS) $(CXXFLAGS) $< -o $(patsubst %.c++,%.o,$<)

%.o : %.cp
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< -o $(patsubst %.cp,%.o,$<)

%.o : %.cxx
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< -o $(patsubst %.cxx,%.o,$<)

# Rules for producing the executable.
#----------------------------------------------
$(PROGRAM_1) : $(OBJS)
ifeq ($(strip $(SRCEXTS)), .c)  # C file
	$(CC) -o $(PROGRAM_1) $(OBJS) $(LDFLAGS)
else                            # C++ file
	$(CXX) -o $(PROGRAM_1) $(OBJS) $(LDFLAGS)
endif



rebuild: clean all

clean :
	@$(RM) $(OBJS)

cleanall: clean
	@$(RM) $(PROGRAM_1) $(PROGRAM_1).exe

### End of the Makefile ##  Suggestions are welcome  ## All rights reserved ###
###############################################################################
