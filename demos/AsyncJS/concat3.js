const fs = require('fs');

function readFileAsync(path, encoding) {
  return new Promise(function (resolve, reject) {
    fs.readFile(path, encoding, function (err, data) {
      if (err) reject(err);
      else resolve(data);
    });
  });
}

function writeFileAsync(path, content) {
  return new Promise(function (resolve, reject) {
    fs.writeFile(path, content, function (err) {
      if (err) reject(err);
      else resolve();
    });
  });
}

const args = process.argv.slice(2);

if (args.length !== 3) {
  console.log('Usage: node concat3.js <inputFile1> <inputFile2> <outputFile>');
  process.exit(1);
}

const [inputFile1, inputFile2, outputFile] = args;

async function concatFiles() {
  try {
	  console.log('Reading file1')
    const data1 = await readFileAsync(inputFile1, 'utf8');
	  console.log('Reading file2')
    const data2 = await readFileAsync(inputFile2, 'utf8');
    const combined = data1 + '\n' + data2;
    await writeFileAsync(outputFile, combined);
    console.log(`File ${outputFile} written successfully.`);
  } catch (err) {
    console.error('Error:', err.message);
  }
}

concatFiles();
console.log('Reading file1 concluded')
