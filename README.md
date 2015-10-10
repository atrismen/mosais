# Mosais
## Mosaic Maker

An application for transforming a picture into a unique mosaic.

This program will work on the image formats that are part of the standard
Java ImageIO class: JPG, PNG, GIF, and BMP.

The original file is not changed, but the mosaic can be saved to any of the input
formats.

The mosaic is created by generating a random distribution of points in a field
the size of the image, then re-coloring all pixels of the image the color of the
closest point. This concept is called a [Voronoi Diagram](https://en.wikipedia.org/wiki/Voronoi_diagram) or Voronoi Tesselation.

Two types of distributions are currently supported:
- Pure random distribution using the standard Java random library.
- A uniform distribution using Poisson disc sampling.

## Credits
- The idea behind this application was inspired by [Visualizing Algorithms](http://bost.ocks.org/mike/algorithms/) by Mike Bostock.

- [imgscalr - Java Image Scaling Library](http://www.thebuzzmedia.com/software/imgscalr-java-image-scaling-library/) by The Buzz Media. 

## License
This project is licensed under the GPL v3 
