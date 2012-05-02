SASS_DIR = File.dirname(__FILE__)

sass_path = SASS_DIR
fonts_path = File.join(SASS_DIR, '..', 'static')
fonts_files = ''
relative_assets = true
css_path = File.join(SASS_DIR, '..', 'static')
environment = :production
output_style = :compressed
