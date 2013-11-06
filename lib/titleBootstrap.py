import bpy, sys, os

#Open up the bootstrapConfig.py
filename = os.getcwd()+'/bootstrapConfig.py'
exec(compile(open(filename).read(), filename, 'exec'))
#Set system path to include title
sys.path.append(os.getcwd()+"/../Titles/"+params['__Category']+"/"+params['__TitleName'])

#import the title script and pass params to it.
bpy.data.scenes[0].render.filepath = '//../../../Output/'+params['__OutFileName']+'.avi'
bpy.data.scenes[0].render.resolution_percentage = int(params['__Resolution'])
bpy.data.scenes[0].render.image_settings.file_format = 'AVI_JPEG'

import script
script.init(params)
